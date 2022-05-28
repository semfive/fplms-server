using DiscussionService.Contracts;
using DiscussionService.Data;
using DiscussionService.Models;
using Microsoft.EntityFrameworkCore;

namespace DiscussionService.Repositories
{
    public class AnswerRepository : RepositoryBase<Answer>, IAnswerRepository
    {
        public AnswerRepository(RepositoryContext repositoryContext) : base(repositoryContext)
        {

        }

        public void CreateAnswer(Answer answer)
        {
            Create(answer);
        }

        public void DeleteAnswer(Answer answer)
        {
            Delete(answer);
        }

        public async Task<IEnumerable<Answer>> GetAllAnswersAsync()
        {
            return await FindByCondition(answer => answer.Removed == false).ToListAsync();
        }

        public async Task<Answer> GetAnswerByIdAsync(Guid answerId)
        {
            return await FindByCondition(answer => answer.Id.Equals(answerId)).FirstOrDefaultAsync();
        }

        public async Task<IEnumerable<Answer>> GetAnswersByStudentId(Guid studentId)
        {
            return await FindByCondition(answer => answer.StudentId.Equals(studentId))
                           .ToListAsync();
        }

        public async Task<IEnumerable<Answer>> GetAnswersRemovedByLecturerId(Guid lecturerId)
        {
            return await FindByCondition(answer => answer.RemovedBy.Equals(lecturerId))
                           .ToListAsync();
        }

        public void UpdateAnswer(Answer answer)
        {
            Update(answer);
        }
    }
}