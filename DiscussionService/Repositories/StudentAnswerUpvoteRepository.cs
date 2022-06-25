using DiscussionService.Contracts;
using DiscussionService.Data;
using DiscussionService.Models;
using Microsoft.EntityFrameworkCore;

namespace DiscussionService.Repositories
{
    public class StudentAnswerUpvoteRepository : RepositoryBase<StudentAnswerUpvote>, IStudentAnswerUpvoteRepository
    {
        public StudentAnswerUpvoteRepository(RepositoryContext repositoryContext) : base(repositoryContext)
        {

        }

        public async void CreateStudentAnswerUpvote(StudentAnswerUpvote studentUpvote)
        {
            Create(studentUpvote);
        }

        public void DeleteStudentAnswerUpvote(StudentAnswerUpvote studentUpvote)
        {
            Delete(studentUpvote);
        }

        public async Task<StudentAnswerUpvote> GetStudentAnswerUpvote(StudentAnswerUpvote dto)
        {
            return await FindByCondition(studentUpvote => studentUpvote.AnswerId == dto.AnswerId && studentUpvote.StudentId == dto.StudentId).FirstOrDefaultAsync();
        }

        public void UpdateStudentAnswerUpvote(StudentAnswerUpvote studentUpvote)
        {
            throw new NotImplementedException();
        }
    }
}