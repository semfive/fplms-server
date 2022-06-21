using DiscussionService.Dtos;
using DiscussionService.Helpers;
using DiscussionService.Models;

namespace DiscussionService.Contracts
{
    public interface IQuestionRepository : IRepositoryBase<Question>
    {
        Task<PagedList<Question>> GetAllQuestionsAsync(QuestionsQueryStringParameters queryStringParameters);
        Task<Question> GetQuestionByIdAsync(Guid questionId, string mode = "");
        Task<IEnumerable<Question>> GetQuestionsByStudentId(Guid studentId);
        Task<IEnumerable<Question>> GetQuestionsRemovedByLecturer(string lecturerEmail);
        void CreateQuestion(Question question);
        void UpdateQuestion(Question question);
        void DeleteQuestion(Question question);
    }
}